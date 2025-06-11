import { useState } from "react";

import Header from "../Header";
import Navigation from "../Navigation";
import Footer from "../Footer";

import "./index.scss";

interface PageWrapperProps {
  children: React.ReactNode;
}

const PageWrapper: React.FC<PageWrapperProps> = ({ children }) => {
  const [isOpen, setIsOpen] = useState(false);

  const toggleMenu = () => {
    setIsOpen(!isOpen);
  };

  return (
    <div className="page-wrapper">
      <Header isSearchVisible isAuth={false} toggleMenu={toggleMenu} />
      <div className="main-wrapper">
        <Navigation isOpen={isOpen} />
        <div className="main-wrapper__content">{children}</div>
      </div>
      <Footer />
    </div>
  );
};

export default PageWrapper;
