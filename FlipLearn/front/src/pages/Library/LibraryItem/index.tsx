import "./index.scss";
import { formatDate } from "../../../utils/formatDate";
import lock from "../../../assets/lock.svg";
import { slicedText } from "../../../utils/slicedText";
import { Tooltip } from "@mui/material";

interface Creator {
  id: number;
  username: string;
  email: string;
  trustLevel: any;
  image: string;
}

interface LibraryListProps {
  isFolder: boolean;
  id: string;
  description: string;
  isPublic: boolean;
  title: string;
  label: string;
  cefr: string;
  cardsCount: number;
  cards: any[];
  creationDate: string;
  creator?: Creator;
  showCreator?: boolean;
  onClick: () => void;
}

const LibraryItem: React.FC<LibraryListProps> = ({
  isFolder,
  description,
  title,
  isPublic,
  label,
  creationDate,
  onClick,
  cefr,
  cardsCount,
  creator,
  showCreator = false,
}) => {
  return (
    <div className="library-item relative" onClick={onClick}>
      <div className="library-top">
        <div className="library-top__intro">
          <div className="flex gap-3">
            <span>{title}</span>
            {!isPublic && <img src={lock} alt="lock" />}
          </div>

          <div className="flex gap-2">
            <span className="library-level">{label}</span>
          </div>
        </div>
        <div className="library-top__terms">
          <span>
            {cardsCount ? cardsCount : 0} {isFolder ? "sets" : "terms"}
          </span>
          {isFolder && (
            <span style={{ backgroundColor: "#F3D86D" }}>{cardsCount ? cardsCount : 0} terms</span>
          )}

          <span className="library-level !bg-[#F3D86D]">{cefr}</span>
        </div>
      </div>

      <Tooltip title={description.length > 40 && description}>
        <span>{slicedText(description, 60)}</span>
      </Tooltip>

      <div className="library-bottom flex w-full justify-between absolute bottom-[10px] !justify-end">
        {showCreator && creator && (
          <div className="library-bottom__user flex gap-1 items-center flex-1">
            <span
              className="user-photo w-6 h-6 rounded-full flex items-center justify-center text-white text-xs font-medium"
              style={{ backgroundColor: creator.image }}
            >
              {creator.username.charAt(0).toUpperCase()}
            </span>
            <span className="user-name">{creator.username}</span>
          </div>
        )}

        <span className="text-[#4F4F4F] text-sm pr-5">{formatDate(creationDate)}</span>
      </div>
    </div>
  );
};

export default LibraryItem;
