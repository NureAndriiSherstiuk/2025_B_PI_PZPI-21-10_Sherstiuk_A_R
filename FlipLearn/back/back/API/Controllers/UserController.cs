using AutoMapper;
using back.Core.Application.Services;
using back.Core.Domain.DTO;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using System.Security.Claims;

namespace back.API.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class UserController : ControllerBase
    {
        private readonly UserService userService;
        private IMapper mapper;
        public UserController(UserService userService, IMapper mapper)
        {
            this.userService = userService;
            this.mapper = mapper;
        }

        [HttpGet]
        [Authorize]
        public async Task<IActionResult> GetUser()
        {
            int userId = int.Parse(User.FindFirstValue(ClaimTypes.NameIdentifier));
            UserWithoutPassword? user = await userService.GetUserWithoutPasswordAsync(userId);

            if(user is null)
                return NotFound(new {Error = "No such user"});
            
            return Ok(user);
        }

        [HttpGet("byUsernameEmail")]
        public async Task<IActionResult> GetUsers(string query, int offset)
        {
            List<UserMinimal> users = await userService.GetMinimalUsersAsync(query, offset);

            return Ok(users);
        }
    }
}
