using back.API.Requests;
using back.Core.Application.Services;
using back.Core.Domain.Models;
using back.Core.Domain.Records;
using FluentValidation;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using System.Security.Claims;

namespace back.API.Controllers
{
    public class RegAuthController : ControllerBase
    {
        private readonly UserService _userService;
        private readonly EmailCodeService _codeService;

        public RegAuthController(UserService userService, EmailCodeService codeService)
        {
            _userService = userService;
            _codeService = codeService;
        }

        [HttpPost("login-user")]
        public async Task<IActionResult> AuthorizeUser([FromForm] LoginData loginData)
        {
            User? user = await _userService.GetUserByEmailAndPasswordAsync(loginData.email, loginData.password);
            if (user is not null)
            {
                var response = JWTService.GenerateToken(user);
                return Ok(response);
            }
            return BadRequest(new { Error = "Invalid login or password" });
        }

        [HttpPost("new-user")]
        public async Task<IActionResult> RegisterUser([FromBody] RegistrationRequest request,  [FromServices] IValidator<RegistrationRequest> validator)
        {
            var validationRes = validator.Validate(request);
            if (!validationRes.IsValid)
                return BadRequest(new { Erorrs = validationRes.Errors.Select(el => el.ErrorMessage).ToList() });

            if (request.Code is null)
            {
                (bool exists, string? field) = await _userService.CheckUserExistence(request.Email, request.Username);

                if (exists)
                    return BadRequest(new { Error = $"User with this {field} already exists" });

                DateTime expiration = await _codeService.SendAndSaveCode(request.Email);
                return Ok(new { Message = "Send your credentials and the code you recieved on your email before expiration time", expiration });
            }

            if (!await _codeService.ConfirmCode(request.Email, request.Code))
                return BadRequest(new {Error = "Wrong code passed or the code has expired" });
            
            User? returnedUser = await _userService.InsertUserAsync(
                new User()
                {
                    Username = request.Username,
                    Email = request.Email,
                    Password = request.Password,
                }
            );

            await _codeService.RemoveCode(request.Email);

            return await AuthorizeUser(new LoginData(request.Email, request.Password));
        }

        [HttpPatch("new-password")]
        public async Task<IActionResult> ChangePassword([FromBody] ChangePasswordRequest request, [FromServices] IValidator<ChangePasswordRequest> validator)
        {
            var validationRes = validator.Validate(request);
            if (!validationRes.IsValid)
                return BadRequest(new { Erorrs = validationRes.Errors.Select(el => el.ErrorMessage).ToList() });

            if (!await _codeService.ConfirmCode(request.Email, request.Code))
                return BadRequest(new { Error = "Wrong code passed or the code has expired" });

            await _userService.UpdatePasswordAsync(request.Email, request.Password);
            await _codeService.RemoveCode(request.Email);

            return Ok(new {Message = "Password updated"});
        }

        [HttpGet("email-code")]
        public async Task<IActionResult> GetEmailCode([FromQuery] string email)
        {
            if(await _userService.GetUserByEmailAsync(email) is null)
                return BadRequest(new {Error = "User with this email is not registered"});

            DateTime expiration = await _codeService.SendAndSaveCode(email);
            return Ok(new { Message = $"Code was sent to your email and will expire at {expiration}"});
        }

        [HttpPatch("new-image")]
        [Authorize]
        public async Task<IActionResult> ChangeImage([FromQuery] string image)
        {
            int userId = int.Parse(User.FindFirstValue(ClaimTypes.NameIdentifier));

            await _userService.UpdateImageAsync(userId, image);
            return Ok(new { Message = "Image updated" });
        }

        [HttpPatch("new-username")]
        [Authorize]
        public async Task<IActionResult> ChangeUsername([FromQuery] string username)
        {
            (bool exists, string? field) = await _userService.CheckUserExistence(null, username);

            if (exists)
                return BadRequest(new { Error = $"User with this {field} already exists" });

            int userId = int.Parse(User.FindFirstValue(ClaimTypes.NameIdentifier));

            await _userService.UpdateUsernameAsync(userId, username);
            return Ok(new { Message = "Username updated" });
        }
    }
}
