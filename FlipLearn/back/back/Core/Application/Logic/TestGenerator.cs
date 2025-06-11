using AutoMapper;
using back.Core.Application.Business_Entities;
using back.Core.Application.Logic.Questions;
using back.Core.Domain.Models;

namespace back.Core.Application.Logic
{
    public class TestGenerator
    {
        private const int lowestPossibleAnswersNumberForMultipleChoice = 3;

        public static Test GenerateSimpleTest(int questionsNumber, 
            IList<IQuestionGenerator> questionTypes, 
            List<Dictionary> dictionaries, IMapper mapper)
        {
            for (int i = 0; i < dictionaries.Count; i++)
            {
                if((dictionaries[i].FromLang != dictionaries[0].FromLang)
                    || (dictionaries[i].ToLang != dictionaries[0].ToLang))
                {
                    throw new Exception("Different languages in dictionaries");
                }
            }
            List<Card> totalCards = new();

            foreach (var dictionary in dictionaries)
                totalCards.AddRange(dictionary.Cards);

            if (questionsNumber > totalCards.Count)
                throw new Exception("Number of requested questions is bigger " +
                    "than cards amount in selected dictionaries");

            if(questionTypes.Any(q => q is MultipleChoiceQuestionGenerator))
            {
                var cardsWithTranslation = totalCards.Where(card => card.Translation is not null).ToList();
                var cardsWithMeaning = totalCards.Where(card => card.Meaning is not null).ToList();

                if (cardsWithMeaning.Count < lowestPossibleAnswersNumberForMultipleChoice
                    && cardsWithTranslation.Count < lowestPossibleAnswersNumberForMultipleChoice)
                {
                    throw new Exception($"Dictionary or joined dictionaries must contain at least " +
                        $"{lowestPossibleAnswersNumberForMultipleChoice} cards with only translation or only meaning " +
                        $"to generate question with multiple answers");
                }
            }

            List<Card> remainingCards = new(totalCards);

            Test test = new();
            test.FromLanguage = dictionaries[0].FromLang;
            test.ToLanguage = dictionaries[0].ToLang;
            test.DictionariesInvolved = mapper.Map<List<DictionaryForRoom>>(dictionaries);

            List<Question> testQuestions = new();
            for (int i = 0; i < questionTypes.Count; i++)
            {
                int amount = i == questionTypes.Count - 1 ?
                    questionsNumber - ((questionsNumber / questionTypes.Count) * i) :
                    questionsNumber / questionTypes.Count;
                
                testQuestions.AddRange(questionTypes[i]
                    .GenerateQuestions(amount, remainingCards, totalCards));
            }
            test.Questions = testQuestions;
            test.Generated = DateTime.Now;
            return test;
        }
    }
}
