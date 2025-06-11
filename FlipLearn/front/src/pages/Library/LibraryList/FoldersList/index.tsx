import LibraryMain from "..";
import LibraryItem from "../../LibraryItem";

const FoldersList = () => (
  <LibraryMain title="Ваші папки">
    {/* {[1, 2, 3, 4].map(() => (
      <LibraryItem isFolder />
    ))} */}
    <div>Немає створених папок</div>
  </LibraryMain>
);

export default FoldersList;
