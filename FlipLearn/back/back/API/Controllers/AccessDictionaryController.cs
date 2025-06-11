using AutoMapper;
using back.Core.Domain.Records;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using System.Security.Claims;
using back.Core.Application.Services;
using back.API.Requests;

namespace back.API.Controllers
{
    [ApiController]
    [Route("dictionary-access")]
    public class AccessDictionaryController : ControllerBase
    {
        private readonly AccessService accessService;
        private readonly DictionaryService dictionaryService;
        private IMapper mapper;
        private IConfiguration config;

        public AccessDictionaryController(AccessService accessService, IMapper mapper,
            DictionaryService dictionaryService, IConfiguration config)
        {
            this.accessService = accessService;
            this.mapper = mapper;
            this.dictionaryService = dictionaryService;
            this.config = config;
        }

        [HttpGet]
        [Authorize]
        public async Task<IActionResult> GetUsersWithAccess(int dictionaryId)
        {
            var users = await accessService.GetDictionaryAccessAsync(dictionaryId);

            return Ok(users);
        }

        [HttpPost]
        [Authorize]
        // вызывается только в самый первый раз при решении дать доступ, во всех остальных случаях - апдейт
        public async Task<IActionResult> AddUsersAccess(int dictionaryId, List<AccessData> accessList)
        {
            int userId = int.Parse(User.FindFirstValue(ClaimTypes.NameIdentifier));

            if (await dictionaryService.IsUserCreator(userId, dictionaryId))
            {
                if (!await accessService.AddUsersAccessAsync(dictionaryId, accessList))
                    return StatusCode(500, new { Error = "Unable to add access due to server error" });

                return Ok(new { Message = "Access added" });
            }
            return StatusCode(403, new { Error = "You don't have rights for this operation" });
        }


        [HttpPut]
        [Authorize]
        public async Task<IActionResult> UpdateUsersAccess([FromBody] UpdateUsersAccessRequest request)
        {
            int userId = int.Parse(User.FindFirstValue(ClaimTypes.NameIdentifier));

            request.accessToInsert = request.accessToInsert is null ? [] : request.accessToInsert;
            request.accessToUpdate = request.accessToUpdate is null ? [] : request.accessToUpdate;
            request.accessToDelete = request.accessToDelete is null ? [] : request.accessToDelete;

            if (await dictionaryService.IsUserCreator(userId, request.dictionaryId))
            {
                if (!await accessService.UpdateAccessByCreatorAsync(request.dictionaryId, request.accessToInsert, request.accessToUpdate, request.accessToDelete))
                    return StatusCode(500, new { Error = "Unable to update access due to server error" });

                return Ok(new { Message = "Access updated" });
            }
            // Соавтор может только добавить или удалить читателя.
            // поменять уровень доступа другого юзера или удалить соавтора не может
            else if (await accessService.GetUserPermission(userId, request.dictionaryId) == Access.CoAuthor)
            {
                if (request.accessToInsert is not null && request.accessToInsert.All(el => el.access == "Reader")
                    || request.accessToDelete is not null && request.accessToDelete.All(el => el.access == "Reader"))
                {
                    if (!await accessService.UpdateAccessByCoAuthorAsync(request.dictionaryId, request.accessToInsert, request.accessToDelete))
                        return StatusCode(500, new { Error = "Unable to update access due to server error" });

                    return Ok(new { Message = "Access updated" });
                }
                return StatusCode(406, new { Error = "Co-Authors can only add and delete users with \"Reader\" access" });
            }
            return StatusCode(403, new { Error = "You don't have rights for this operation" });
        }

    }
}
