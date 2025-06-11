using AutoMapper;
using back.Core.Domain.DTO;
using back.Core.Domain.Models;
using back.Core.Application.Business_Entities;
using back.Core.Application.Services;

namespace back.Infrastructure.Mapping
{
    public class MappingProfiles : Profile
    {
        public MappingProfiles()
        {

            CreateMap<DictionaryFromClient, Dictionary>();
            CreateMap<DictionaryToClient, Dictionary>();
            CreateMap<Dictionary, DictionaryForRoom>();

            CreateMap<CardFromClient, Card>();
            CreateMap<CardToClient, CardFromClient>().ReverseMap();
            CreateMap<CardToClient, Card>();

            CreateMap<CardFromClient, CardAIFormat>()
                .ForMember(aiCard => aiCard.Word,
                res => res.MapFrom(src => src.Term))
                .ForMember(aiCard => aiCard.Definition,
                res => res.MapFrom(src => src.Meaning));

            CreateMap<CardToClient, CardAIFormat>()
                .ForMember(aiCard => aiCard.Word,
                res => res.MapFrom(src => src.Term))
                .ForMember(aiCard => aiCard.Definition,
                res => res.MapFrom(src => src.Meaning));

            CreateMap<Room, RoomMinimal>()
                .ForMember(roomMin => roomMin.FromLanguage, res => res.MapFrom(room => room.Test.FromLanguage))
                .ForMember(roomMin => roomMin.ToLanguage, res => res.MapFrom(room => room.Test.ToLanguage))
                .ForMember(roomMin => roomMin.QuestionsCount, res => res.MapFrom(room => room.Test.Questions.Count))
                .ForMember(roomMin => roomMin.DictionariesCount, res => res.MapFrom(room => room.Test.DictionariesInvolved.Count))
                .ForMember(roomMin => roomMin.ParticipantsCount, res => res.MapFrom(room => room.Participants.Count))
                .ForMember(roomMin => roomMin.RaceDurationInSec, res => res.MapFrom(room => room.Test.Questions.Count * 20));

            CreateMap<Room, MongoRoom>()
                .ForMember(mongoRoom => mongoRoom.CreatorId, res => res.MapFrom(room => room.Creator.Id))
                .ForMember(mongoRoom => mongoRoom.ParticipantsIds, res => res.MapFrom(room => room.Results.Select(el => el.Participant.Id)));

            CreateMap<MongoRoom, ArchivedRoomMin>()
                .ForMember(archivedRoom => archivedRoom.MongoRoomId, res => res.MapFrom(mongoRoom => mongoRoom.MongoRoomId.ToString()))
                .ForMember(archivedRoom => archivedRoom.FromLanguage, res => res.MapFrom(room => room.Test.FromLanguage))
                .ForMember(archivedRoom => archivedRoom.ToLanguage, res => res.MapFrom(room => room.Test.ToLanguage))
                .ForMember(archivedRoom => archivedRoom.QuestionsCount, res => res.MapFrom(room => room.Test.Questions.Count))
                .ForMember(archivedRoom => archivedRoom.CEFRMin,
                           res => res.MapFrom(room => CEFRLevelHelper.GetMinLevel(room.Test.DictionariesInvolved.Select(d => d.CEFR))))
                .ForMember(archivedRoom => archivedRoom.CEFRMax,
                           res => res.MapFrom(room => CEFRLevelHelper.GetMaxLevel(room.Test.DictionariesInvolved.Select(d => d.CEFR))));

            CreateMap<MongoRoom, ArchivedRoom>()
                .ForMember(archivedRoom => archivedRoom.MongoRoomId, res => res.MapFrom(mongoRoom => mongoRoom.MongoRoomId.ToString()));
        }
    }
}
