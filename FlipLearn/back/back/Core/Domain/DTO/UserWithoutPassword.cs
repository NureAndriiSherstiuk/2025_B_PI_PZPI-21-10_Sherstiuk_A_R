namespace back.Core.Domain.DTO
{
    public class UserWithoutPassword
    {
        public int Id { get; set; }
        public string Username { get; set; }
        public string Email { get; set; }
        public DateTime RegDate { get; set; }
        public string? TrustLevel { get; set; }
        public string? Image { get; set; }
        public string Lang { get; set; }
    }
}
