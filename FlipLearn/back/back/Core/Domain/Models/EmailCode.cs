namespace back.Core.Domain.Models
{
    public class EmailCode
    {
        public string Email { get; set; }
        public string Code { get; set; }
        public DateTime ExpirationTime { get; set; }
    }
}
