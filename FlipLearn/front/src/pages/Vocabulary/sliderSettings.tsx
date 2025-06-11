import { SampleNextArrow, SamplePrevArrow } from "../../components/SliderArrows/arrows";

const sliderSettings = {
  dots: false,
  arrows: true,
  slidesToShow: 1,
  slidesToScroll: 1,
  initialSlide: 0,
  className: "words-slider",
  nextArrow: <SampleNextArrow />,
  prevArrow: <SamplePrevArrow />,
};

export default sliderSettings;
