import React, { useState } from "react";

interface FlipCardProps {
  word: string;
  translate: string;
}

const FlipCard: React.FC<FlipCardProps> = ({ word, translate }) => {
  const [isFlipped, setIsFlipped] = useState(false);

  const handleFlip = () => {
    setIsFlipped(!isFlipped);
  };

  return (
    <div
      className={`card-container ${isFlipped ? "flipped" : ""}`}
      onClick={handleFlip}
      style={{
        perspective: "1000px",
        width: "100%",
        height: "350px",
        cursor: "pointer",
      }}
    >
      <div
        className="card"
        style={{
          width: "100%",
          height: "100%",
          position: "relative",
          transformStyle: "preserve-3d",
          transition: "transform 0.6s cubic-bezier(0.4, 0.2, 0.2, 1)",
          transform: isFlipped ? "rotateY(180deg)" : "rotateY(0deg)",
        }}
      >
        <div
          className="card-face card-front"
          style={{
            position: "absolute",
            width: "100%",
            height: "100%",
            backfaceVisibility: "hidden",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            fontSize: "28px",
            fontWeight: "600",
            borderRadius: "16px",
            border: "1px solid lightgrey",
            textAlign: "center",
            fontFamily: "system-ui, -apple-system, sans-serif",
          }}
        >
          <span>{word}</span>
        </div>

        <div
          className="card-face card-back"
          style={{
            position: "absolute",
            width: "100%",
            height: "100%",
            backfaceVisibility: "hidden",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            fontSize: "24px",
            fontWeight: "500",
            borderRadius: "16px",
            transform: "rotateY(180deg)",
            textAlign: "center",
            border: "1px solid lightgrey",
            fontFamily: "system-ui, -apple-system, sans-serif",
          }}
        >
          <span>{translate}</span>
        </div>
      </div>
    </div>
  );
};

export default FlipCard;
