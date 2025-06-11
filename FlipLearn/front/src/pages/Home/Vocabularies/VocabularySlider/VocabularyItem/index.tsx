import "./index.scss";

interface VocabularyItemProps {
  title: string;
  description: string;
  cefr: string;
  label: string;
}

const VocabularyItem: React.FC<VocabularyItemProps> = ({ title, description, cefr, label }) => {
  return (
    <div className="vocabulary">
      <div className="vocabulary-reduction">
        <span className="vocabulary-reduction__title">{title}</span>
        <span className="vocabulary-reduction__terms text-[#6B6B6B] text-[12px]">{description}</span>
      </div>

      <div className="vocabulary-creator absolute bottom-[10px] w-full items-center">
        <span>{label}</span>
        <span className="vocabulary-creator__name bg-[#F3D86D] text-[#4F4F4F] rounded-[6px] py-2 px-3 mr-5">
          {cefr}
        </span>
      </div>
    </div>
  );
};

export default VocabularyItem;
