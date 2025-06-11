namespace back.Core.Domain.Records
{
    public enum QDirection : byte
    {
        TermToTranslation = 1,
        TermToMeaning = 2,
        TranslationToTerm = 3,
        MeaningToTerm = 4
    }
}
