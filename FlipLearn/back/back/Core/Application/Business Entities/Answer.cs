namespace back.Core.Application.Business_Entities
{
    public class Answer
    {
        public bool IsCorrect { get; set; }
    }

    public class TrueFalseAnswer : Answer
    {
        public bool Choice { get; set; }
    }

    public class MultipleChoiceAnswer : Answer
    {
        public string Text { get; set; }
    }

    public class HandwrittenAnswer : Answer
    {
        public string CorrectInput { get; set; }
    }

    public class AudioAnswer : Answer
    {
        public string CorrectAnswer { get; set; }
    }

    public class VoiceAnswer : Answer
    {
        public string CorrectAnswer { get; set; }
    }
}
