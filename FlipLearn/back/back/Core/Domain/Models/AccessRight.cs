namespace back.Core.Domain.Models
{
    public class AccessRight
    {
        public int UserId { get; set; }
        public int DictionaryId { get; set; }
        public string Access { get; set; }
    }
}
