namespace back.Core.Application.Services
{
    public static class CEFRLevelHelper
    {
        private static readonly Dictionary<string, int> levelOrder = new()
        {
            { "A1", 1 },
            { "A2", 2 },
            { "B1", 3 },
            { "B2", 4 },
            { "C1", 5 },
            { "C2", 6 }
        };

        public static string? GetMinLevel(IEnumerable<string?> levels)
        {
            return levels
                .Where(level => level != null && levelOrder.ContainsKey(level))
                .OrderBy(level => levelOrder[level!])
                .FirstOrDefault();
        }

        public static string? GetMaxLevel(IEnumerable<string?> levels)
        {
            return levels
                .Where(level => level != null && levelOrder.ContainsKey(level))
                .OrderByDescending(level => levelOrder[level!])
                .FirstOrDefault();
        }
    }

}
