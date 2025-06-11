import arrowLeft from "../../assets/arrow-left.png";
import arrowRight from "../..//assets/arrow-right.png";
import { FC } from "react";

interface ArrowProps {
  className?: string;
  onClick?: () => void;
}

export const SampleNextArrow: FC<ArrowProps> = ({ className, onClick }) => {
  return (
    <img
      src={arrowRight}
      className={className}
      onClick={onClick}
      style={{ right: 0, zIndex: 1000 }}
      alt="Next Arrow"
    />
  );
};

export const SamplePrevArrow: FC<ArrowProps> = ({ className, onClick }) => {
  return (
    <img
      src={arrowLeft}
      className={className}
      onClick={onClick}
      style={{ left: 0, zIndex: 1000 }}
      alt="Prev Arrow"
    />
  );
};
