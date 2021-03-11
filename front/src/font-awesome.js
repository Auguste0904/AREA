import { library } from "@fortawesome/fontawesome-svg-core";
import { fas } from "@fortawesome/free-solid-svg-icons";
import { far } from "@fortawesome/free-regular-svg-icons";
import { fab } from '@fortawesome/free-brands-svg-icons';
import { faEdit, faTrashAlt } from '@fortawesome/free-regular-svg-icons';
import { faArrowRight } from '@fortawesome/free-solid-svg-icons';
import FontAwesomeIcon from "@/Components/FontAwesomeIcon.vue";

library.add(fas, far, fab, faEdit, faTrashAlt, faArrowRight);

export { FontAwesomeIcon };