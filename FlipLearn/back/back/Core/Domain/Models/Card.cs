
namespace back.Core.Domain.Models
{
    public class Card
    {
        public long Id { get; set; }
        public int DictionaryId { get; set; }
        public string Term { get; set; }
        public string? Meaning { get; set; }
        public string? Translation { get; set; }
        public string Status { get; set; }
    }
}
