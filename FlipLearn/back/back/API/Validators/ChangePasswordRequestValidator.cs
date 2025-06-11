using back.API.Requests;
using FluentValidation;

namespace back.API.Validators
{
    public class ChangePasswordRequestValidator : AbstractValidator<ChangePasswordRequest>
    {
        public ChangePasswordRequestValidator()
        {
            RuleFor(x => x.Email)
                .NotEmpty()
                .EmailAddress()
                .Matches("^[^@\\s]+@ethereal\\.email$");

            RuleFor(x => x.Password)
                .NotEmpty()
                .MinimumLength(8);

            RuleFor(x => x.Code)
                .NotEmpty();
        }
    }
}
