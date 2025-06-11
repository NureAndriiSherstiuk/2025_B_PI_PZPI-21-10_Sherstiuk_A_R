using back.Core.Domain.DTO;
using Microsoft.AspNetCore.SignalR;
using System.Security.Claims;

namespace back.API.Hubs
{
    public abstract class IdentityHub<T> : Hub<T> where T : class
    {
        protected UserMinimal DetermineUser(HubCallerContext context)
        {
            return new UserMinimal()
            {
                Id = int.Parse(context.UserIdentifier),
                Username = context.User.FindFirst(ClaimTypes.Name).Value,
                Email = context.User.FindFirst(ClaimTypes.Email).Value,
                TrustLevel = context.User.FindFirst("TrustLevel").Value,
                Image = context.User.FindFirst("Image").Value,
            };
        }
    }
}
