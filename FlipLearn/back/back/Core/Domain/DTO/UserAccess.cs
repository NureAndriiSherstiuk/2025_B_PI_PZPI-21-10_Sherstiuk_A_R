namespace back.Core.Domain.DTO
{
    public class UserAccess
    {
        public int Id { get; set; }
        public string Username { get; set; }
        public string Email { get; set; }
        public string? Image { get; set; }
        public string Access { get; set; }
    }
}
