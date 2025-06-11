using back.Core.Domain.Models;
using back.Core.Domain.Records;
using back.Core.Domain.Repositories;
using back.Infrastructure.Persistance.Repositories;
using MailKit.Net.Smtp;
using MailKit.Security;
using MimeKit;
using System.Security;
using System.Security.Cryptography;

namespace back.Core.Application.Services
{
    public class EmailCodeService
    {
        private static readonly string _systemEmailUserName = "Cleve Emmerich";
        private static readonly string _systemEmailAdress = "cleve.emmerich@ethereal.email";
        private static readonly string _systemEmailPassword = "zekss7wCBCAt1J4fNs";
        private static readonly TimeSpan _codeLifeTime = TimeSpan.FromMinutes(3);

        private readonly IEmailCodeRepository _emailCodeRepository;
        public EmailCodeService(IEmailCodeRepository emailCodeRepository)
        {
            _emailCodeRepository = emailCodeRepository;
        }

        public async Task<DateTime> SendAndSaveCode(string email)
        {
            var message = new MimeMessage();

            message.From.Add(
                new MailboxAddress(_systemEmailUserName, _systemEmailAdress)
                );
            message.To.Add(
                new MailboxAddress("Dear User", email)
                );
            message.Subject = "FlipLearn email confirmation code";
            
            var bodyBuilder = new BodyBuilder();

            DateTime expirationTime = DateTime.Now + _codeLifeTime;
            string code = GenerateNumericCode();

            string body = $"Your code: {code}{Environment.NewLine}The code will expire at {expirationTime.ToString("F")}";

            bodyBuilder.TextBody = body;

            message.Body = bodyBuilder.ToMessageBody();

            using var client = new SmtpClient();
            client.Connect("smtp.ethereal.email", 587, SecureSocketOptions.StartTls);
            client.Authenticate(_systemEmailAdress, _systemEmailPassword);
            
            await _emailCodeRepository.AddOrUpdateCode(email, code, expirationTime);

            client.Send(message);
            client.Disconnect(true);

            return expirationTime;
        }

        public async Task<bool> ConfirmCode(string email, string code)
        {
            EmailCode? emailCode = await _emailCodeRepository.GetCodeAsync(email);

            if (emailCode is null || 
                emailCode.Code != code || 
                (DateTime.Now - _codeLifeTime > emailCode.ExpirationTime))
                return false;
            
            return true;
        }

        public async Task RemoveCode(string email)
        {
            await _emailCodeRepository.RemoveCode(email);
        }

        private string GenerateNumericCode()
        {
            int size = 8;
            var code = new char[size];
            using (var rng = RandomNumberGenerator.Create())
            {
                var bytes = new byte[size];
                rng.GetBytes(bytes);

                for (int i = 0; i < size; i++)
                    code[i] = (char)('0' + (bytes[i] % 10));
            }

            return new string(code);
        }
    }
}
