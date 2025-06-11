namespace back.API.Requests
{
    public class ChangePasswordRequest
    {
        public string Email { get; set; }
        public string Password { get; set; }
        public string Code { get; set; }
    }
}
