import { createRoute as createRoute_1 } from "@vaadin/hilla-file-router/runtime.js";
import type { AgnosticRoute as AgnosticRoute_1 } from "@vaadin/hilla-file-router/types.js";
import * as Page_1 from "../views/@index.js";
import * as Layout_1 from "../views/@layout.js";
import * as Page_2 from "../views/candidates.js";
import * as Page_3 from "../views/EvaluationResult.js";
import * as Page_4 from "../views/face2facetest.js";
import * as Page_5 from "../views/FaceRegisterDialog.js";
import * as Page_6 from "../views/FaceVerificationDialog.js";
import * as Page_7 from "../views/interviewResult.js";
import * as Page_8 from "../views/JavaCodeEditor.js";
import * as Page_9 from "../views/ModifyCv.js";
import * as Page_10 from "../views/NewCv.js";
import * as Page_11 from "../views/newInterViewDialog.js";
import * as Page_12 from "../views/ResumeForm.js";
import * as Page_13 from "../views/resumeRewriter.js";
import * as Page_14 from "../views/spokenlanguage.js";
import * as Page_15 from "../views/talkToAzure.js";
import * as Page_16 from "../views/writetest.js";
const routes: readonly AgnosticRoute_1[] = [
    createRoute_1("", Layout_1, [
        createRoute_1("", Page_1),
        createRoute_1("candidates", Page_2),
        createRoute_1("EvaluationResult", Page_3),
        createRoute_1("face2facetest", Page_4),
        createRoute_1("FaceRegisterDialog", Page_5),
        createRoute_1("FaceVerificationDialog", Page_6),
        createRoute_1("interviewResult", Page_7),
        createRoute_1("JavaCodeEditor", Page_8),
        createRoute_1("ModifyCv", Page_9),
        createRoute_1("NewCv", Page_10),
        createRoute_1("newInterViewDialog", Page_11),
        createRoute_1("ResumeForm", Page_12),
        createRoute_1("resumeRewriter", Page_13),
        createRoute_1("spokenlanguage", Page_14),
        createRoute_1("talkToAzure", Page_15),
        createRoute_1("writetest", Page_16)
    ])
];
export default routes;
