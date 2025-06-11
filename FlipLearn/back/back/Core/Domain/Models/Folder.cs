using back.Core.Domain.DTO;

namespace back.Core.Domain.Models
{
    public class Folder
    {
        public int Id { get; set; }
        public string Title { get; set; }
        public string Description { get; set; }
        public int CreatorId { get; set; }
        public bool IsPublic { get; set; }
        public ICollection<DictionaryDto> Dictionaries { get; set; }
    }
}
