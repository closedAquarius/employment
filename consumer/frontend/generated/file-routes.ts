import { createRoute as createRoute_1 } from "@vaadin/hilla-file-router/runtime.js";
import type { AgnosticRoute as AgnosticRoute_1 } from "@vaadin/hilla-file-router/types.js";
import * as Page_1 from "../views/@index.js";
import * as Layout_1 from "../views/@layout.js";
import * as Page_2 from "../views/EvaluationResult.js";
import * as Page_3 from "../views/interview.js";
import * as Page_4 from "../views/interviewResult.js";
import * as Page_5 from "../views/JavaCodeEditor.js";
import * as Page_6 from "../views/writetest.js";
const routes: readonly AgnosticRoute_1[] = [
    createRoute_1("", Layout_1, [
        createRoute_1("", Page_1),
        createRoute_1("EvaluationResult", Page_2),
        createRoute_1("interview", Page_3),
        createRoute_1("interviewResult", Page_4),
        createRoute_1("JavaCodeEditor", Page_5),
        createRoute_1("writetest", Page_6)
    ])
];
export default routes;
