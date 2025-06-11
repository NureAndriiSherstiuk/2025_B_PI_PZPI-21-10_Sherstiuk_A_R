namespace back.API.Requests
{
    public class GetTestRequest
    {
        public int questionsNumber { get; set; }
        public List<int> questionTypes { get; set; }
        public List<int> dictionariesId { get; set; }
    }
}
