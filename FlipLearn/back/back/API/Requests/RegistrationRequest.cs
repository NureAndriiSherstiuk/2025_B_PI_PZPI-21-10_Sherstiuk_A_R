namespace back.API.Requests
{
    public class RegistrationRequest
    {
        public required string Username { get; set; }
        public required string Email { get; set; }
        public required string Password { get; set; }
        public string? Code { get; set; } = null;
    }
}
