using System.Text.Json.Serialization;
using back.Core.Domain.Records;
using MongoDB.Bson.Serialization.Attributes;

namespace back.Core.Application.Business_Entities
{
    [JsonPolymorphic(TypeDiscriminatorPropertyName = "type")]
    [JsonDerivedType(typeof(TrueFalseQuestion), typeDiscriminator: "trueFalse")]
    [JsonDerivedType(typeof(MultipleChoiceQuestion), typeDiscriminator: "multipleChoice")]
    [JsonDerivedType(typeof(HandwrittenQuestion), typeDiscriminator: "handwritten")]
    [JsonDerivedType(typeof(AudioQuestion), typeDiscriminator: "audio")]
    [JsonDerivedType(typeof(VoiceQuestion), typeDiscriminator: "voice")]
    [JsonDerivedType(typeof(AnsweredTrueFalseQuestion), typeDiscriminator: "answeredTrueFalse")]
    [JsonDerivedType(typeof(AnsweredMultipleChoiceQuestion), typeDiscriminator: "answeredMultipleChoice")]
    [JsonDerivedType(typeof(AnsweredHandwrittenQuestion), typeDiscriminator: "answeredHandwritten")]
    [JsonDerivedType(typeof(AnsweredAudioQuestion), typeDiscriminator: "answeredAudio")]
    [JsonDerivedType(typeof(AnsweredVoiceQuestion), typeDiscriminator: "answeredVoice")]
    [BsonKnownTypes(typeof(TrueFalseQuestion), typeof(MultipleChoiceQuestion), typeof(HandwrittenQuestion), typeof(AudioQuestion), typeof(VoiceQuestion))]
    public class Question
    {
        public QDirection Direction { get; set; }
        public string Term { get; set; }
        public string? Meaning { get; set; }
        public string? Translation { get; set; }
    }

    [BsonKnownTypes(typeof(AnsweredTrueFalseQuestion))]
    public class TrueFalseQuestion : Question
    {
        public List<TrueFalseAnswer> Answers { get; set; }
    }

    [BsonKnownTypes(typeof(AnsweredMultipleChoiceQuestion))]
    public class MultipleChoiceQuestion : Question
    {
        public List<MultipleChoiceAnswer> Answers { get; set; }
    }

    [BsonKnownTypes(typeof(AnsweredHandwrittenQuestion))]
    public class HandwrittenQuestion : Question
    {
        public HandwrittenAnswer Answer { get; set; }
    }

    [BsonKnownTypes(typeof(AnsweredAudioQuestion))]
    public class AudioQuestion : Question
    {
        public AudioAnswer Answer { get; set; }
    }

    [BsonKnownTypes(typeof(AnsweredVoiceQuestion))]
    public class VoiceQuestion : Question
    {
        public VoiceAnswer Answer { get; set; }
    }



    public class AnsweredTrueFalseQuestion : TrueFalseQuestion
    {
        public bool IsGivenCorrectAnswer { get; set; }
        public string? GivenAnswer { get; set; }
    }

    public class AnsweredMultipleChoiceQuestion : MultipleChoiceQuestion
    {
        public bool IsGivenCorrectAnswer { get; set; }
        public string? GivenAnswer { get; set; }
    }

    public class AnsweredHandwrittenQuestion : HandwrittenQuestion
    {
        public bool IsGivenCorrectAnswer { get; set; }
        public string? GivenAnswer { get; set; }
    }

    public class AnsweredAudioQuestion : AudioQuestion
    {
        public bool IsGivenCorrectAnswer { get; set; }
        public string? GivenAnswer { get; set; }
    }

    public class AnsweredVoiceQuestion : VoiceQuestion
    {
        public bool IsGivenCorrectAnswer { get; set; }
        public string? GivenAnswer { get; set; }
    }
}
