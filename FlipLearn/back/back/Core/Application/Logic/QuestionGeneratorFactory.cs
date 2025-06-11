using back.Core.Application.Logic.Questions;
using back.Core.Domain.Records;

namespace back.Core.Application.Logic
{
    public static class QuestionGeneratorFactory
    {
        public static IQuestionGenerator GetGenerator(QTypes type)
        {
            return type switch
            {
                QTypes.TrueFalse => new TrueFalseQuestionGenerator(),
                QTypes.MultipleChoice => new MultipleChoiceQuestionGenerator(),
                QTypes.Handwritten => new HandwrittenQuestionGenerator(),
                QTypes.Audio => new AudioQuestionGenerator(),
                QTypes.Voice => new VoiceQuestionGenerator(),
                _ => throw new ArgumentException("Unknown question type")
            };
        }
    }
}
