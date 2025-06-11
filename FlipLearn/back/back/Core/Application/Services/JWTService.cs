using back.Core.Domain.Models;
using Microsoft.IdentityModel.Tokens;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;

namespace back.Core.Application.Services
{
    public class JWTService
    {
        public static object GenerateToken(User user)
        {
            var claims = new List<Claim> {
                    new (ClaimTypes.Name, user.Username),
                    new (ClaimTypes.NameIdentifier, user.Id.ToString()),
                    new (ClaimTypes.Email, user.Email),
                    new ("TrustLevel",  user.TrustLevel ?? string.Empty),
                    new ("Image", user.Image ?? string.Empty)
            };

            var jwt = new JwtSecurityToken(
                    issuer: Environment.GetEnvironmentVariable("Auth-Issuer"),
                    audience: Environment.GetEnvironmentVariable("Auth-Audience"),
                    claims: claims,
                    expires: DateTime.UtcNow.Add(TimeSpan.FromHours(10)),
                    signingCredentials: new SigningCredentials(
                        new SymmetricSecurityKey(
                            Encoding.UTF8.GetBytes(Environment.GetEnvironmentVariable("Auth-Key"))
                            ),
                        SecurityAlgorithms.HmacSha256)
                    );

            var encodedJwt = new JwtSecurityTokenHandler().WriteToken(jwt);

            var response = new
            {
                token = encodedJwt,
                username = user.Username,
                email = user.Email,
                lang = user.Lang
            };

            return response;
        }
    }
}
