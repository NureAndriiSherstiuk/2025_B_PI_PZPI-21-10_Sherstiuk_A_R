namespace back.Core.Domain.DTO
{
    public class UserMinimal
    {
        public int Id { get; set; }
        public string Username { get; set; }
        public string Email { get; set; }
        public string? TrustLevel { get; set; }
        public string? Image { get; set; }
    }
}
