using AutoMapper;
using AutoMapper.Internal;
using back.Core.Domain.Models;
using back.Core.Domain.Records;
using back.Infrastructure.Persistance.Repositories;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.IdentityModel.Tokens;
using System.Security.Claims;
using back.Core.Domain.DTO;
using back.Core.Application.Services;
using back.API.Requests;
using System.Security;

namespace back.API.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class DictionaryController : ControllerBase
    {
        private readonly DictionaryService dictionaryService;
        private readonly AccessService accessService;
        private IMapper mapper;

        public DictionaryController(DictionaryService dictionaryService, IMapper mapper, AccessService accessService)
        {
            this.dictionaryService = dictionaryService;
            this.mapper = mapper;
            this.accessService = accessService;
        }

        [HttpGet]
        public async Task<IActionResult> GetDictionariesByQuery([FromQuery] GetDictionariesRequest request)
        {
            List<DictionaryDto> dictionaries = await dictionaryService.GetDictionariesAsync(request);

            return Ok(dictionaries);
        }

        [HttpGet("full")]
        public async Task<IActionResult> GetFullDictionary(int dictionaryId)
        {
            DictionaryExtended? dictionary = await dictionaryService.GetFullDictionaryAsync(dictionaryId);

            if (dictionary is null)
                return BadRequest("No such dictionary");

            if(!dictionary.IsPublic)
            {
                if (!int.TryParse(User.FindFirstValue(ClaimTypes.NameIdentifier), out int userId))
                    return Unauthorized();
                
                if (await dictionaryService.IsUserCreator(userId, dictionaryId))
                    return Ok(new { dictionary, access = "Creator" });

                Access access = await accessService.GetUserPermission(userId, dictionaryId);

                return access switch
                {
                    Access.None => StatusCode(403, "You don't have access for this dictionary"),
                    _ => Ok(new { dictionary, access = access.ToString() })
                };
            }

            return Ok(new { dictionary, access = "Public" });
        }

        [HttpGet("own")]
        [Authorize]
        public async Task<IActionResult> GetUserDictionaries()
        {
            int userId = int.Parse(User.FindFirstValue(ClaimTypes.NameIdentifier));

            List<DictionaryDto> dictionaries = await dictionaryService.GetUsersDictionariesAsync(userId);

            return Ok(dictionaries);
        }


        [HttpGet("available")]
        [Authorize]
        public async Task<IActionResult> GetUserAvailableDictionaries()
        {
            int userId = int.Parse(User.FindFirstValue(ClaimTypes.NameIdentifier));

            List<DictionaryDto> dictionaries = await dictionaryService.GetUsersAvailableDictionariesAsync(userId);

            return Ok(dictionaries);
        }

        [HttpPost]
        [Authorize]
        public async Task<IActionResult> CreateDictionary(
            DictionaryFromClient _dictionary,
            [FromServices] AIСorrectnessService сorrectnessChecker)
        {
            if (_dictionary.Cards.Count < 3)
            {
                return BadRequest("Cards count must be 3 or above");
            }
            foreach (var card in _dictionary.Cards)
            {
                if (card.Meaning.IsNullOrEmpty()
                    && card.Translation.IsNullOrEmpty())
                {
                    return BadRequest("Card must either contain meaning, translation or both");
                }
            }

            int userId = int.Parse(User.FindFirstValue(ClaimTypes.NameIdentifier));

            if (!await dictionaryService.InsertDictionaryWithCardsAsync(_dictionary, userId, сorrectnessChecker))
                return StatusCode(500, new { Error = "Unable to insert new dictionary" });


            return Ok(new { Message = "Object created" });
        }

        [HttpPut]
        [Authorize]
        public async Task<IActionResult> UpdateDictionary(
            [FromBody] UpdateDictionaryRequest request,
            [FromServices] AIСorrectnessService сorrectnessChecker)
        {
            // надо написать мидлвар чтобы если прилетает либо пустой
            // список на вставку либо null то приводилось к чему-то одному

            request.CardsToInsert = request.CardsToInsert is null ? [] : request.CardsToInsert;
            request.CardsToUpdate = request.CardsToUpdate is null ? [] : request.CardsToUpdate;
            request.CardsToDelete = request.CardsToDelete is null ? [] : request.CardsToDelete;

            foreach (var card in request.CardsToInsert.Concat(request.CardsToUpdate.Select(card => mapper.Map<CardFromClient>(card))))
            {
                if (card.Term.IsNullOrEmpty() || card.Meaning.IsNullOrEmpty()
                    && card.Translation.IsNullOrEmpty())
                {
                    return BadRequest(new { Error = "Card must contain termnin and either contain meaning, translation or both" });
                }
            }
            var langsChange = (request.From, request.To);
            int userId = int.Parse(User.FindFirstValue(ClaimTypes.NameIdentifier));

            if (!await dictionaryService.UpdateDictionaryWithCardsAsync(
                request.DictionaryId, request.NewTitle, request.NewDescription,
                request.IsPublic, langsChange, request.NewLabel, request.NewCEFR,
                request.CardsToInsert, request.CardsToUpdate, request.CardsToDelete, сorrectnessChecker))
                return StatusCode(500, new { Error = "Unable to update dictionary" });


            return Ok(new { Message = "Object updated" });
        }

        [HttpDelete]
        [Authorize]
        public async Task<IActionResult> DeleteDictionary(int dictionatyId)
        {
            int userId = int.Parse(User.FindFirstValue(ClaimTypes.NameIdentifier));

            if (await dictionaryService.IsUserCreator(userId, dictionatyId))
            {
                if (!await dictionaryService.DeleteDictionaryWithCardsAsync(dictionatyId))
                    return StatusCode(500, new { Error = "Unable to delete dictionary" });

                return Ok(new { Message = "Object deleted" });
            }

            return StatusCode(403, new { Error = "You don't have access for this dictionary" });
        }

        [HttpPatch("visibility")]
        [Authorize]
        public async Task<IActionResult> ChangeVisibility(int dictionatyId, [FromServices] AIСorrectnessService сorrectnessService)
        {
            int userId = int.Parse(User.FindFirstValue(ClaimTypes.NameIdentifier));

            if (await dictionaryService.IsUserCreator(userId, dictionatyId))
            {
                await dictionaryService.ChangeDictionaryVisibility(dictionatyId, сorrectnessService);

                return Ok(new { Message = "Visibility changed" });
            }

            return StatusCode(403, new { Error = "You don't have access to this dictionary" });
        }
    }
}
