import clsx from "clsx";
import "./index.scss";

interface LibraryMainProps {
  title: string;
  children: React.ReactNode;
  listStyles?: string;
}

const LibraryMain: React.FC<LibraryMainProps> = ({ title, children, listStyles }) => (
  <div className="library-main">
    <span className="library-main__title">{title}</span>
    <div className={clsx("library-list", listStyles && listStyles)}>{children}</div>
  </div>
);

export default LibraryMain;
