namespace back.Core.Domain.DTO
{
    public class DictionaryDto
    {
        public int Id { get; set; }
        public string Title { get; set; }
        public DateTime CreationDate { get; set; }
        public int CreatorId { get; set; }
        public UserMinimal Creator { get; set; }
        public string? Description { get; set; }
        public bool IsPublic { get; set; }
        public DateTime? LastModified { get; set; }
        public string FromLang { get; set; }
        public string ToLang { get; set; }
        public string Label { get; set; }
        public string? CEFR { get; set; }
        public int CardsCount { get; set; }
    }
}
