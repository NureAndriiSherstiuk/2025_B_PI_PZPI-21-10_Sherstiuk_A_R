import Slider from "react-slick";

import sliderSettings from "./sliderSettings";
import VocabularyItem from "./VocabularyItem";

import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";
import "./index.scss";
import { useEffect, useState } from "react";
import axios from "axios";

const VocabularySlider = () => {
  const [vocabularies, setVocabularies] = useState([]);

  useEffect(() => {
    getVocabularies();
  }, []);

  const getVocabularies = () => {
    axios
      .get("https://localhost:7288/Dictionary", {
        params: {
          Take: 5,
        },
      })
      .then((response) => {
        console.log(response);
        setVocabularies(response.data);
      })
      .catch((err) => console.log(err));
  };

  return (
    <Slider {...sliderSettings}>
      {vocabularies.map((vocabulary: any) => (
        <VocabularyItem
          title={vocabulary.title}
          description={vocabulary.description}
          cefr={vocabulary.cefr}
          label={vocabulary.label}
        />
      ))}
    </Slider>
  );
};

export default VocabularySlider;
