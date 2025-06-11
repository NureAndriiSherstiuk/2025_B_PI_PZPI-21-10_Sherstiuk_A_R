namespace back.Core.Application.Business_Entities
{
    public class DictionaryForRoom
    {
        public int Id { get; set; }
        public string Title { get; set; }
        public int CreatorId { get; set; }
        public string? CEFR {  get; set; }
    }
}
