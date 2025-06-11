namespace back.Core.Domain.DTO
{
    public class CardToClient
    {
        public long CardId { get; set; }
        public string Term { get; set; }
        public string? Meaning { get; set; }
        public string? Translation { get; set; }
        public string Status { get; set; }

    }
}
