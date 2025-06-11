export const slicedText = (text: string, lettersCount: number) => {
  return text.length > lettersCount ? `${text.slice(0, lettersCount)}...` : text;
};
