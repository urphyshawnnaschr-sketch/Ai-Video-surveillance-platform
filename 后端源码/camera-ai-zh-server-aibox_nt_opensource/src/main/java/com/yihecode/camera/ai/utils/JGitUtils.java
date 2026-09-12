package com.yihecode.camera.ai.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class JGitUtils {

    private static final String username = "git_model@yihecode.cn";
    private static final String password = System.getenv("SOURCE_GIT_PASSWORD");

    /**
* Generate Body copy Info
*/
    private static final CredentialsProvider provider = new UsernamePasswordCredentialsProvider(username, password);

    public static Git openRpo(String localPath) {
        Git git = null;
        try {
            Repository repository = new FileRepositoryBuilder()
                    .setGitDir(Paths.get(localPath, ".git").toFile())
                    .build();
            git = new Git(repository);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return git;
    }

    /**
* Init (init)--git init
*/
    public static void init(String localPath) throws GitAPIException {
        Git.init().setDirectory(new File(localPath)).call();
    }

    /**
* Add to Temp Store Area (Add)git add.
* git add Delete.txt
* Delete and Move File not can make Use git.add(), Need make Use git.rm() Mode, just Compute Param is"."Also Need make Use git.rm() Method
*/
    public static void add(String localPath, String fileName) throws Exception {
        openRpo(localPath).add().addFilepattern(Optional.ofNullable(fileName).orElse(".")).call();
    }

    public static void rm(String localPath, String fileName) throws Exception {
        openRpo(localPath).rm().addFilepattern(fileName).call();
    }

    /**
* Submit (Commit) git commit -m"first commit"
*/
    public static void commit(String localPath, String commitInfo) throws Exception {
        openRpo(localPath).commit().setMessage(Optional.ofNullable(commitInfo).orElse("default commit info")).call();

    }

    /**
* Move (mv)
*/
    public static void mv(String localPath, String sourcePath, String targetPath, File file) {
        System.out.println(sourcePath);
        try {
            File newDir = new File(targetPath);
            if (!newDir.exists()) {
                newDir.mkdirs();
            }
            File newFile = new File(newDir, file.getName());
            boolean success = file.renameTo(newFile);
            if (success) {
                add(localPath, ".");
                rm(localPath, sourcePath);
                commit(localPath, "File moved to new directory");
                push(localPath);
            } else {
                //handle error
log.error("File Move Exception!");
}
} catch (Exception e) {
e.printStackTrace();
}
}

/**
* Status (status) git status
*
* @throws Exception
*/
public static Map<String, String> status(String localPath) throws Exception {
Map<String, String> map = new HashMap<>();
Status status = openRpo(localPath).status().call();
map.put("Added", status.getAdded().toString());
map.put("Changed", status.getChanged().toString());
map.put("Conflicting", status.getConflicting().toString());
map.put("ConflictingStageState", status.getConflictingStageState().toString());
map.put("IgnoredNotInIndex", status.getIgnoredNotInIndex().toString());
map.put("Missing", status.getMissing().toString());
map.put("Modified", status.getModified().toString());
map.put("Removed", status.getRemoved().toString());
map.put("UntrackedFiles", status.getUntracked().toString());
map.put("UntrackedFolders", status.getUntrackedFolders().toString());
return map;
}

/**
* =============================== part Support Operation =============================
*/
/**
* Create part Support (Create Branch) git branch dev
*
* @throws Exception
*/
public static void branch(String localPath, String branchName) throws Exception {
openRpo(localPath).branchCreate()
.setName(branchName)
.call();
}

/**
* Delete part Support (Delete Branch) git branch -d dev
*
* @throws Exception
*/
public static void delBranch(String localPath, String branchName) throws Exception {
openRpo(localPath).branchDelete()
.setBranchNames(branchName)
.call();
}

/**
* Cut change part Support (Checkout Branch) git checkout dev
*
* @throws Exception
*/
public static void checkoutBranch(String localPath, String branchName) throws Exception {
openRpo(localPath).checkout()
.setName(branchName)
.call();
}

/**
* All part Support (BranchList) git branch
*
* @throws Exception
*/
public static List<Ref> listBranch(String localPath) throws Exception {
return openRpo(localPath).branchList().call();
}

/**
* combine and part Support (Merge Branch) git merge dev
*
* @throws Exception
*/
public static void mergeBranch(String localPath, String branchName, String commitMsg) throws Exception {
// Cut change part Support Get part Support Info Store in Ref Object In
Ref refdev = openRpo(localPath).checkout().setName(branchName).call();
// Cut change return main part Support
openRpo(localPath).checkout().setName("main").call();
// combine and Target part Support
MergeResult mergeResult = openRpo(localPath).merge().include(refdev)
// same Hour Submit
.setCommit(true)
// part Support combine and Strategy NO_FF instead table Normal combine and, FF instead table fast Speed combine and
.setFastForward(MergeCommand.FastForwardMode.NO_FF)
.setMessage(Optional.ofNullable(commitMsg).orElse("master Merge"))
.call();
}

/**
* ======================== far End Warehouse database Operation (Repository)==============================
*/
/**
* Push (Push) git push origin master
*
* @throws Exception
*/
public static void push(String localPath) throws Exception {
openRpo(localPath).push()
// Set Push URL Name like"origin"
.setRemote("origin")
// Set Need Push part Support, like Result far End not has rule Create
.setRefSpecs(new RefSpec("main"))
// Body copy Verify
.setCredentialsProvider(provider)
.call();
}

public static void push(String localPath, String branch) throws Exception {
openRpo(localPath).push()
// Set Push URL Name like"origin"
.setRemote("origin")
// Set Need Push part Support, like Result far End not has rule Create
.setRefSpecs(new RefSpec(branch))
// Body copy Verify
.setCredentialsProvider(provider)
.call();
}

/**
* / Pull Get (Pull) git pull origin
*
* @throws Exception
*/
public static void pull(String localPath, String remotePath) throws Exception {
// Determine localPath Whether Exist, does not exist Call clone Method
File directory = new File(localPath);
if (!directory.exists()) {
gitClone(localPath, remotePath,"main");
}
openRpo(localPath).pull()
.setRemoteBranchName("main")
.setCredentialsProvider(provider)
.call();
}

public static void pull(String localPath, String remotePath, String branch) throws Exception {
// Determine localPath Whether Exist, does not exist Call clone Method
File directory = new File(localPath);
if (!directory.exists()) {
gitClone(localPath, remotePath, branch);
}
openRpo(localPath).pull()
.setRemoteBranchName(branch)
.setCredentialsProvider(provider)
.call();
}

/**
* Clone (Clone) git clone https://xxx.git
*
* @throws Exception
*/
public static void gitClone(String localPath, String remotePath, String branch) throws Exception {
// Clone
Git git = Git.cloneRepository()
.setURI(remotePath)
.setDirectory(new File(localPath))
.setCredentialsProvider(provider)
// Set Whether Clone child Warehouse database
.setCloneSubmodules(true)
// Set Clone part Support
.setBranch(branch)
.call();
// Close source, with Explain Put Local Warehouse database Lock
git.getRepository().close();
git.close();

}
public static void init(String localPath, String remotePath, String branch, String sparseCheckoutPath) throws GitAPIException, IOException {
// File directory = new File(localPath);
// if (directory.exists()) {
// return;
//}
Git git = Git.init()
.setDirectory(new File(localPath))
.setInitialBranch(branch).call();

// Get Warehouse database Config
StoredConfig config = git.getRepository().getConfig();
config.setString("remote","origin","url", remotePath);
config.setString("remote","origin","fetch","+refs/heads/*:refs/remotes/origin/*");
config.setString("branch", branch,"remote","origin");
config.setString("branch", branch,"merge","refs/heads/"+ branch);
config.setBoolean("core", null,"bare", false);
config.setBoolean("core", null,"ignorecase", true);
if (StringUtils.isNoneBlank(sparseCheckoutPath)) {
config.setBoolean("core", null,"sparseCheckout", true);
File gitInfoFile = new File(localPath +"/.git/info");
if (!gitInfoFile.exists()) gitInfoFile.mkdirs();
File excludeFile = new File(localPath +"/.git/info/exclude");
if (!excludeFile.exists()) excludeFile.createNewFile();
File sparseCheckoutFile = new File(localPath +"/.git/info/sparse-checkout");
if (!sparseCheckoutFile.exists()) {
sparseCheckoutFile.createNewFile();
}
FileWriter writer = new FileWriter(sparseCheckoutFile, true);
writer.write(sparseCheckoutPath +"\n");
writer.close();
}
config.save();
git.getRepository().close();
git.close();
}
public static void setSparseCheckoutPath(String localPath, String sparseCheckoutPath) throws IOException {
// Git git = openRpo(localPath);
// StoredConfig config = git.getRepository().getConfig();
// config.setBoolean("core", null,"sparseCheckout", true);
// config.setStringList("core", null,"sparseCheckoutPath", Arrays.asList(sparseCheckoutPath));
// config.save();
// git.getRepository().close();
// git.close();
File gitInfoFile = new File(localPath +"/.git/info");
if (!gitInfoFile.exists()) gitInfoFile.mkdirs();
File sparseCheckoutFile = new File(localPath +"/.git/info/sparse-checkout");
if (!sparseCheckoutFile.exists()) {
sparseCheckoutFile.createNewFile();
}
FileWriter writer = new FileWriter(sparseCheckoutFile, true);
writer.write(sparseCheckoutPath +"\n");
writer.close();
}

public static void fetchCommand(String localPath) throws GitAPIException {
Git git = openRpo(localPath);
FetchCommand fetchCommand = git.fetch();
FetchResult result = fetchCommand.setRemote("origin")
.setRefSpecs(new RefSpec("+refs/heads/*:refs/remotes/origin/*"))
.setCredentialsProvider(provider)
.call();
git.getRepository().close();
git.close();
}
// public static boolean needPull(String localPath) throws Exception{
// File directory = new File(localPath);
// if (!directory.exists()) {
// return true;
//}
// Git git = openRpo(localPath);
// Repository localRepo = git.getRepository();
// // Get Local Warehouse database most new Submit
// RevWalk revWalk = new RevWalk(localRepo);
// ObjectId localId = localRepo.resolve("HEAD");
// if (null == localId) return true;
// RevCommit headCommit = revWalk.parseCommit(localId);
//
// // Get Local Warehouse database most new Submit Tree Object
// CanonicalTreeParser headTree = new CanonicalTreeParser();
// headTree.reset(headCommit.getRawBuffer());
//
// // Get Line up Warehouse database most new Submit (like Result has Word)
// Iterable<RevCommit> latestCommits = git.log().all().call();
// if (latestCommits!= null) {
// RevCommit latestRemoteCommit = latestCommits.iterator().next();
//
// // Get Line up Warehouse database most new Submit Tree Object
// CanonicalTreeParser remoteTree = new CanonicalTreeParser();
// remoteTree.reset(latestRemoteCommit.getRawBuffer());
//
// // Compare Two Submit of between Difference
// List<DiffEntry> diffEntries = git.diff()
//.setNewTree(remoteTree)
//.setOldTree(headTree)
//.call();
//
// // Check Whether has Difference
// if (!diffEntries.isEmpty()) {
// System.out.println("Local Warehouse database and Line up Warehouse database of between Exist Difference. Need Execute pull Operation.");
// return true;
//} else {
// System.out.println("Local Warehouse database and Line up Warehouse database of between not has Difference. not Need Execute pull Operation.");
// return false;
//}
//} else {
// System.out.println("not has most new Submit.");
// return false;
//}
//}

// public static void download(String localPath, String filePath) throws Exception{
// Repository repository = null;
// try {
// // Create Git Warehouse database Object
// FileRepositoryBuilder builder = new FileRepositoryBuilder();
// repository = builder.setGitDir(new File(localPath)) // Set Local Warehouse database Path
////.setWorkTree(new File(filePath)) // Set work Make Tree Path
//.readEnvironment() // Read Environment Variable
//.findGitDir() // check find.git Directory
//.build();
// // Login to GitLab Warehouse database
// Git git = new Git(repository);
//
// // Download File
// File downloadFile = new File(localPath, filePath.substring(filePath.lastIndexOf('/') + 1));
// FileOutputStream outputStream = new FileOutputStream(downloadFile);
// byte[] buffer = new byte[1024];
// int bytesRead;
// ObjectId fileId = repository.resolve("HEAD");
// if (fileId == null) {
// System.out.println("File not find to");
// throw new Exception("File not find to");
//}
// InputStream inputStream = repository.open(fileId).openStream();
// while ((bytesRead = inputStream.read(buffer))!= -1) {
// outputStream.write(buffer, 0, bytesRead);
//}
// outputStream.close();
// System.out.println("File Download Complete Complete:"+ downloadFile.getAbsolutePath());
//
//} catch (IOException e) {
// e.printStackTrace();
// throw new Exception("Download Exception");
//} finally {
// // Close Warehouse database Connection
// if (repository!= null) {
// repository.close();
//}
//}
//}
//
// public static void download(String localPath, String remotePath, String branch, String filePath) throws Exception {
// Repository repository = null;
// try {
// // Init Git Object
// Git git = Git.cloneRepository()
//.setURI(remotePath)
//.setBranch(branch)
//.setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password))
//.call();
//
// // Get Warehouse database Object
// repository = git.getRepository();
// // Parse File Corresponding ObjectId
//// ObjectId objectId = repository.resolve(branch +":"+ filePath);
// ObjectId objectId = repository.resolve(branch +":"+"HEAD");
// if (objectId == null) {
// System.out.println("File not find to");
// throw new Exception("File not find to");
//}
// // Read File Content and Save to Local
// try (FileOutputStream fos = new FileOutputStream(localPath)) {
// repository.open(objectId).copyTo(fos);
//}
// System.out.println("File Download success");
//} catch (GitAPIException | IOException e) {
// e.printStackTrace();
// throw new Exception("Download Exception");
//} finally {
// if (repository!= null) {
// repository.close();
//}
//}
//}

}
