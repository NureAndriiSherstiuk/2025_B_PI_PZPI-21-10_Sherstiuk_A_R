using back.Core.Domain.DTO;
using back.Core.Domain.Models;

namespace back.API.Hubs.Interfaces
{
    public interface IRoomClient
    {
        Task NotifyUserConnected(UserMinimal user);
        Task NotifyUserDisconnected(UserMinimal user);
        Task StartRace(DateTime startTime, int totalSeconds);
        Task UpdateRemainingTime(int remainigSeconds);
        Task CollectAnswersAndFinishRace();
        Task ParticipantFinished(UserMinimal participant, int correctCount, int wrongCount, TimeSpan time);
        Task GetRaceResults(ICollection<ParticipantRaceResult> results);
        Task RoomWasDissolved();
        Task ReceiveRoom(Room room, bool isCreator);
        Task ShowCurrentResults(IEnumerable<object> quickResults);
    }
}
