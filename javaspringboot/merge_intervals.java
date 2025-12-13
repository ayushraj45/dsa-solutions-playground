//Problem: Given an array of time intervals for server downtime [[1,3],[2,6],[8,10],[15,18]], merge all overlapping intervals.

public int[][] merge(int[][] intervals) {
    if (intervals.length <= 1) return intervals;
    
    // Sort by start time
    Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));
    
    List<int[]> result = new ArrayList<>();
    int[] current = intervals[0];
    result.add(current);
    
    for (int[] interval : intervals) {
        if (interval[0] <= current[1]) { // Overlap
            current[1] = Math.max(current[1], interval[1]);
        } else { // No overlap, move to next
            current = interval;
            result.add(current);
        }
    }
    return result.toArray(new int[result.size()][]);
}
