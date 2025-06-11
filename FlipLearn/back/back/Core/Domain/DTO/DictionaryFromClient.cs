namespace back.Core.Domain.DTO
{
    public class DictionaryFromClient
    {
        public string Title { get; set; }
        public string? Description { get; set; }
        public bool IsPublic { get; set; }
        public string FromLang { get; set; }
        public string ToLang { get; set; }
        public string Label { get; set; }
        public string? CEFR { get; set; }
        public List<CardFromClient> Cards { get; set; }
    }
}
