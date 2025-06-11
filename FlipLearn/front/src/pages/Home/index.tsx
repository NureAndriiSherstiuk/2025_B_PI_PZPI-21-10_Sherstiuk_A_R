import Header from "../../components/Header";
import Intro from "./Intro";
import Approve from "./Approve";
import Educating from "./Educating";
import Vocabularies from "./Vocabularies";
import Learning from "./Learning";
import Remember from "./Remember";
import Footer from "../../components/Footer";

const Home = () => (
  <>
    <Header welcome isSearchVisible />
    <Intro />
    <Approve />
    <Educating />
    <Vocabularies />
    <Learning />
    <Remember />
    <Footer />
  </>
);

export default Home;
