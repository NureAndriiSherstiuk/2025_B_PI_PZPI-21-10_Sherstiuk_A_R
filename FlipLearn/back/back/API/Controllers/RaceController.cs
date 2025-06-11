using back.Core.Application.Services;
using Microsoft.AspNetCore.Mvc;
using back.Core.Domain.Models;
using Microsoft.AspNetCore.Authorization;
using System.Security.Claims;
using MongoDB.Bson;

namespace back.API.Controllers
{
    [ApiController]
    [Route("races")]
    public class RaceController : ControllerBase
    {
        private RoomService roomService;
        public RaceController(RoomService roomService)
        {
            this.roomService = roomService;
        }

        [HttpGet("{roomId}")]
        [Authorize]
        public async Task<IActionResult> GetFinishedRace([FromRoute] string roomId)
        {
            ArchivedRoom room = await roomService.GetFinishedRoomAsync(new ObjectId(roomId));
            return Ok(room);
        }

        [HttpGet("finished")]
        [Authorize]
        public async Task<IActionResult> GetUsersFinishedRaces()
        {
            int userId = int.Parse(User.FindFirstValue(ClaimTypes.NameIdentifier));

            List<ArchivedRoomMin> rooms = await roomService.GetFinishedRoomsAsync(userId);
            return Ok(rooms);
        }

        /// <summary>
        /// Only for testing purposes
        /// </summary>
        /// <returns></returns>
        [HttpPost]
        public async Task<IActionResult> AddFinishedRoom(Room room)
        {
            await roomService.InsertRoomAsync(room);
            return Ok(new { Message = "Room added" });
        }
    }
}
