import VocabulariesList from "../LibraryList/VocabulariesList";
// import FoldersList from "../LibraryList/FoldersList";

import "./index.scss";

const LibraryWrapper = () => (
  <div className="list-blocks">
    <VocabulariesList />
    {/* <FoldersList /> */}
  </div>
);

export default LibraryWrapper;
