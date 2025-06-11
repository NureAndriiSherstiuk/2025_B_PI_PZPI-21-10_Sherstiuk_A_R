using Microsoft.Data.SqlClient;
using Microsoft.Extensions.Options;

namespace back.Infrastructure.Persistance.DbConnections
{
    public class MsSQLConnectionWrapper : IDisposable
    {
        public SqlConnection Connection { get; }

        public MsSQLConnectionWrapper(IOptions<MsSQLConnectionOptions> options)
        {
            Connection = new SqlConnection(options.Value.ConnectionString);
            Connection.Open();
        }

        public void Dispose()
        {
            Connection.Close();
        }
    }

    public class MsSQLConnectionOptions
    {
        public required string ConnectionString { get; set; }
    }
}
