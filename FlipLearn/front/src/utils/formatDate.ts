import { format, parseISO } from "date-fns";

export const formatDate = (dateString: string): string => {
  if (!dateString) return "Invalid Date";

  try {
    const parsedDate = parseISO(dateString.split(" ")[0]); // Берем только дату без времени
    return format(parsedDate, "dd/MM/yy");
  } catch (error) {
    console.error("Ошибка парсинга даты:", error);
    return "Invalid Date";
  }
};
