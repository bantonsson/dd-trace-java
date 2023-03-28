package datadog.trace.civisibility

import datadog.trace.api.civisibility.CIProviderInfo

class BitBucketInfoTest extends CITagsProviderImplTest {

  @Override
  CIProviderInfo instanceProvider() {
    return new BitBucketInfo()
  }

  @Override
  String getProviderName() {
    return BitBucketInfo.BITBUCKET_PROVIDER_NAME
  }

  @Override
  Map<String, String> buildRemoteGitInfoEmpty() {
    final Map<String, String> map = new HashMap<>()
    map.put(BitBucketInfo.BITBUCKET, "true")
    map.put(BitBucketInfo.BITBUCKET_WORKSPACE_PATH, localFSGitWorkspace)
    return map
  }

  @Override
  Map<String, String> buildRemoteGitInfoMismatchLocalGit() {
    final Map<String, String> map = new HashMap<>()
    map.put(BitBucketInfo.BITBUCKET, "true")
    map.put(BitBucketInfo.BITBUCKET_WORKSPACE_PATH, localFSGitWorkspace)
    map.put(BitBucketInfo.BITBUCKET_GIT_COMMIT, "0000000000000000000000000000000000000000")
    return map
  }
}
