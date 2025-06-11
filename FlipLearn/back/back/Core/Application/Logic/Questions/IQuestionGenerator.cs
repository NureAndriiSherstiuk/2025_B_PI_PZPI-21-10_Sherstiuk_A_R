using back.Core.Domain.Models;
using back.Core.Application.Business_Entities;

namespace back.Core.Application.Logic.Questions
{
    public interface IQuestionGenerator
    {
        List<Question> GenerateQuestions(int amount, List<Card> remainingCardsToUse, IReadOnlyList<Card> totalCards);
    }
}
