//Problem: Design a transfer(fromId, toId, amount) function using pseudo-code or Java/Node that handles database consistency.
async function transfer(fromId, toId, amount) {
  const client = await db.connect();
  try {
    await client.query('BEGIN'); // Start Transaction

    // Lock the rows to prevent race conditions (Pessimistic Lock)
    // Key skill: Lock in consistent order (e.g. by ID) to avoid Deadlocks!
    const firstLock = fromId < toId ? fromId : toId;
    const secondLock = fromId < toId ? toId : fromId;

    const fromAccount = await client.query('SELECT balance FROM accounts WHERE id = $1 FOR UPDATE', [firstLock]);
    const toAccount = await client.query('SELECT balance FROM accounts WHERE id = $1 FOR UPDATE', [secondLock]);

    if (fromAccount.balance < amount) throw new Error('Insufficient Funds');

    await client.query('UPDATE accounts SET balance = balance - $1 WHERE id = $2', [amount, fromId]);
    await client.query('UPDATE accounts SET balance = balance + $1 WHERE id = $2', [amount, toId]);

    await client.query('COMMIT');
  } catch (e) {
    await client.query('ROLLBACK');
    throw e;
  } finally {
    client.release();
  }
}
