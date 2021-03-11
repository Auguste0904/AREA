import { createWebHistory, createRouter } from 'vue-router';
import { DASHBOARDPATH, HOMEPATH } from '@/Constant.js';
import Dashboard from '@/Components/Dashboard/Dashboard.vue';
import Home from '@/Components/Home/Home.vue';
import { GOOGLEVERIFYPATH, MAILVERIFYPATH, GITHUBVERIFYPATH, TRELLOVERIFYPATH, GITLABVERIFYPATH} from './Constant';
import Verify from './Components/Verify/Verify.vue';
import GitHubVerify from '@/Components/Verify/GitHubVerify.vue';
import TrelloVerify from '@/Components/Verify/TrelloVerify.vue';
import GitLabVerify from '@/Components/Verify/GitLabVerify.vue';
import MailVerify from './Components/Verify/MailVerify.vue';

const routes = [
    {
        path: HOMEPATH,
        name: 'Home',
        component: Home,
    },
    {
        path: DASHBOARDPATH,
        name: 'Dashboard',
        component: Dashboard,
    },
    {
        path: GOOGLEVERIFYPATH,
        name: 'GoogleVerify',
        component: Verify,
    },
    {
        path: MAILVERIFYPATH,
        name: 'MailVerify',
        component: MailVerify,
    },
    {
        path: GITHUBVERIFYPATH,
        name: 'GithubVerify',
        component: GitHubVerify,
    },
    {
        path: TRELLOVERIFYPATH,
        name: 'TrelloVerify',
        component: TrelloVerify,
    },
    {
        path: GITLABVERIFYPATH,
        name: 'GitLabVerify',
        component: GitLabVerify,
    },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router;