using back.Core.Domain.DTO;

namespace back.API.Requests
{
    public class UpdateDictionaryRequest
    {
        public int DictionaryId { get; set; }
        public string NewTitle { get; set; }
        public string NewDescription { get; set; }
        public bool IsPublic { get; set; }
        public string From { get; set; }
        public string To { get; set; }
        public string NewLabel { get; set; }
        public string? NewCEFR { get; set; }
        public List<CardFromClient>? CardsToInsert { get; set; }
        public List<CardToClient>? CardsToUpdate { get; set; }
        public List<int>? CardsToDelete { get; set; }
    }
}