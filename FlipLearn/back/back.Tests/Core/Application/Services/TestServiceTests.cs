using AutoMapper;
using back.Core.Application.Logic;
using back.Core.Domain.Models;
using back.Core.Application.Business_Entities;
using back.Core.Application.Logic.Questions;
using back.Infrastructure.Mapping;

namespace back.Tests.Core.Application.Services
{
    public class TestServiceTests
    {
        [Theory]
        [MemberData(nameof(GetValidDictionaries))]
        public void GenerateSimpleTest_ValidResult(int questionsNumber,
            IList<IQuestionGenerator> questionTypes,
            List<Dictionary> dictionaries)
        {
            var config = new MapperConfiguration(cfg =>
            {
                cfg.AddProfile<MappingProfiles>();
            });
            IMapper mapper = config.CreateMapper();

            Test test = TestGenerator.GenerateSimpleTest(questionsNumber, questionTypes, dictionaries, mapper);

            Assert.NotNull(test);
            Assert.Equal(questionsNumber, test.Questions.Count);
            Assert.Equal(dictionaries[0].FromLang, test.FromLanguage);
            Assert.Equal(dictionaries[0].ToLang, test.ToLanguage);
            Assert.Distinct(test.Questions);
        }


        [Theory]
        [MemberData(nameof(GetInValidDictionaries))]
        public void GenerateSimpleTest_Exeptions(int questionsNumber,
            IList<IQuestionGenerator> questionTypes,
            List<Dictionary> dictionaries)
        {
            var config = new MapperConfiguration(cfg =>
            {
                cfg.AddProfile<MappingProfiles>();
            });
            IMapper mapper = config.CreateMapper();

            Assert.Throws<Exception>(() =>
                    TestGenerator.GenerateSimpleTest(questionsNumber, questionTypes, dictionaries, mapper));
        }

        public static IEnumerable<object[]> GetValidDictionaries =>
            new List<object[]>
            {
                new object[] { 5,
                    new List<IQuestionGenerator>() { new HandwrittenQuestionGenerator(), 
                                                     new MultipleChoiceQuestionGenerator()},
                    new List<Dictionary>() { 
                        new() { Id = 1, FromLang = "English", ToLang = "Ukrainian", 
                            Cards = [
                                new Card { Id = 1, DictionaryId = 1, Term = "Programming", Translation = "Програмування", Meaning = "Process of writing code", Status = "Confirmed" },
                                new Card { Id = 2, DictionaryId = 1, Term = "clock", Translation = "годинник", Meaning = "a device for measuring and indicating time", Status = "Confirmed" },
                                new Card { Id = 3, DictionaryId = 1, Term = "river", Translation = "річка", Meaning = "a large natural stream of water", Status = "Confirmed" },
                                new Card { Id = 4, DictionaryId = 1, Term = "piracy", Translation = "пиратство", Meaning = "practice of downloading and distributing copyrighted works digitally without permission", Status = "Confirmed" },
                                new Card { Id = 5, DictionaryId = 1, Term = "core component", Translation = "основний компонент", Meaning = null, Status = "Confirmed" },
                                new Card { Id = 6, DictionaryId = 1, Term = "vital", Translation = "життево важливий", Meaning = null, Status = "Confirmed" },
                                new Card { Id = 7, DictionaryId = 1, Term = "embedded", Translation = "влаштований", Meaning = null, Status = "Confirmed" }
                            ]  
                        } 
                    }
                }
            };

        public static IEnumerable<object[]> GetInValidDictionaries =>
            new List<object[]>
            {
                new object[] { 3,
                    new List<IQuestionGenerator>() { new HandwrittenQuestionGenerator(),
                                                     new MultipleChoiceQuestionGenerator()},
                    new List<Dictionary>() {
                        new() { Id = 1, FromLang = "English", ToLang = "Ukrainian",
                            Cards = [
                                new Card { Id = 1, DictionaryId = 1, Term = "Programming", Translation = "Програмування", Meaning = "Process of writing code", Status = "Confirmed" },
                                new Card { Id = 2, DictionaryId = 1, Term = "clock", Translation = "годинник", Meaning = "a device for measuring and indicating time", Status = "Confirmed" },
                                new Card { Id = 3, DictionaryId = 1, Term = "river", Translation = "річка", Meaning = "a large natural stream of water", Status = "Confirmed" },
                            ]
                        },
                        new() { Id = 2, FromLang = "German", ToLang = "Spanish",
                            Cards = [
                                new Card { Id = 4, DictionaryId = 2, Term = "Programmierung", Translation = "programación", Meaning = null, Status = "Confirmed" },
                                new Card { Id = 5, DictionaryId = 2, Term = "die Uhr", Translation = "mirar", Meaning = null, Status = "Confirmed" },
                                new Card { Id = 6, DictionaryId = 2, Term = "Fluss", Translation = "río", Meaning = null, Status = "Confirmed" },
                            ]
                        }
                    }
                },
                new object[] { 10,
                    new List<IQuestionGenerator>() { new HandwrittenQuestionGenerator(),
                                                     new MultipleChoiceQuestionGenerator()},
                    new List<Dictionary>() {
                        new() { Id = 1, FromLang = "English", ToLang = "Ukrainian",
                            Cards = [new Card { Id = 1, DictionaryId = 1, Term = "Programming", Translation = "Програмування", Meaning = "Process of writing code", Status = "Confirmed" },
                            new Card { Id = 2, DictionaryId = 1, Term = "clock", Translation = "годинник", Meaning = "a device for measuring and indicating time", Status = "Confirmed" },
                            new Card { Id = 3, DictionaryId = 1, Term = "river", Translation = "річка", Meaning = "a large natural stream of water", Status = "Confirmed" },
                            new Card { Id = 4, DictionaryId = 1, Term = "piracy", Translation = "пиратство", Meaning = "practice of downloading and distributing copyrighted works digitally without permission", Status = "Confirmed" },
                            new Card { Id = 5, DictionaryId = 1, Term = "core component", Translation = "основний компонент", Meaning = null, Status = "Confirmed" },
                            new Card { Id = 6, DictionaryId = 1, Term = "vital", Translation = "життево важливий", Meaning = null, Status = "Confirmed" },
                            new Card { Id = 7, DictionaryId = 1, Term = "embedded", Translation = "влаштований", Meaning = null, Status = "Confirmed" }]
                        }
                    }
                }
            };
    }
}