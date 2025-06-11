namespace back.API.Requests
{
    public class GetDictionariesRequest
    {
        public int Take { get; set; }
        public int LastId { get; set; }
        public string? TitlePattern {  get; set; }
        public IEnumerable<string>? LabelsPool { get; set; }
        public IEnumerable<string>? LangFromPool { get; set; }
        public IEnumerable<string>? LangToPool { get; set; }
        public DateTime? DateCreatedFrom { get; set; }
        public DateTime? DateCreatedTo { get; set; }
    }
}
